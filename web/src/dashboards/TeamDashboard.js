import React, {useCallback, useEffect, useState} from 'react'
import Config from "../options/Configuration";
import {Spinner, Table} from "reactstrap";
import Application from "../row/Application";
import {useQueryParams} from "hookrouter";

function TeamDashboard({team}) {
    const [queryParams] = useQueryParams();
    const {
        clustergroup = "kubernetes",
        mode = 'CONTROLLER'
    } = queryParams;

    let [isLoading, setIsLoading] = useState(true);
    let [allDashboards, setAllDashboards] = useState(undefined);
    let [dashboard, setDashboard] = useState({});
    let [error, setError] = useState("");
    let [reloading, setReloading] = useState(false);

    function clearError() {

        setError("");
    }

    const getDashboard = useCallback(
        (data) => {
            if (data !== undefined && data["clusterGroupDashboardList"] !== null) {
                if (data["clusterGroupDashboardList"][clustergroup] !== undefined) {
                    return data["clusterGroupDashboardList"][clustergroup]
                }
            }
            console.log("Could not find data for " + clustergroup);
            setError("No data for " + clustergroup);
            return {}
        },
        [clustergroup],
    );

    // Disabled for now. Add to the setTimeout in the reload useEffect to re-enable spinner when updating page.
    function showTimer() {
        setReloading(true);
        setTimeout(() => {
            setReloading(false)
        }, 1000);
    }

    useEffect(fetchDashboard, []);

    useEffect(() => {
        const timer = setTimeout(() => {
            console.log('10 second timer finished');
            fetchDashboard()
        }, 10000);
        return () => clearTimeout(timer);
    });

    useEffect(() => {
        clearError();
        if (allDashboards !== undefined) {
            console.log("Changing dashboard");
            setDashboard(getDashboard(allDashboards))
        }
    }, [clustergroup, allDashboards, getDashboard]);

    function fetchDashboard() {
        console.log("Getting dashboards");
        fetch(Config.getAPIURL() + '/team/' + team)
            .then(
                response => {
                    if (response.status === 200) {
                        return response.json()
                    } else {
                        //bad data
                        console.error("Could not find cluster groups");
                        return false;
                    }
                }
            )
            .then(
                data => {
                    if (data === false) {
                        // Handle error and retry
                    } else {
                        console.log(data);
                        setAllDashboards(data);
                        setDashboard(getDashboard(data));
                        setIsLoading(false);
                    }
                }
            )
            .catch(e => console.log(e));

    }

    if (isLoading) {
        return (
            <div/>
        )
    } else if (error !== "") {
        console.log("ERROR!");
        return (
            <div>
                <h4 className={"text-light " + clustergroup}>{error}</h4>
            </div>
        )
    } else {
        return (
            <div>
                <Table dark className={"table-bordered"}>
                    <thead>
                    <tr>

                        <th><Reloading show={reloading}/></th>
                        {dashboard["clusterGroupEnvironments"].map((environmentHeader, index) => {
                            return (
                                <th key={index}>{environmentHeader}</th>
                            )
                        })}
                    </tr>
                    </thead>
                    <tbody>
                    {dashboard["applications"].map((application, index) => {
                        return (<Application key={index} application={application}
                                             headers={dashboard["clusterGroupEnvironments"]} mode={mode}/>)
                    })}
                    </tbody>
                </Table>
            </div>
        )
    }
}

function Reloading({show}) {
    if (show) {
        return <Spinner color="info" size="sm"/>
    }
    return <span/>
}

export default TeamDashboard