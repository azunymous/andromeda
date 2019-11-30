import React, {useCallback, useEffect, useState} from 'react'
import Config from "../options/Configuration";
import {Table} from "reactstrap";
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
            console.log("Could not find data for " + clustergroup)
            setError("No data for " + clustergroup);
            return {}
        },
        [clustergroup],
    );


    useEffect(fetchDashboard, []);

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
                <Table dark>
                    <thead>
                    <tr>
                        <th/>
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

export default TeamDashboard