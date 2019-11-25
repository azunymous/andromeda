import React, {useEffect, useState} from 'react'
import Config from "../Configuration";
import {Table} from "reactstrap";
import Application from "../row/Application";

function TeamDashboard({team, dataCentre, mode}) {
    let [isLoading, setIsLoading] = useState(true);
    let [dashboard, setDashboard] = useState({});
    let [error, setError] = useState("");

    useEffect(fetchDashboard, []);

    function getDashboard(data) {
        if (data["clusterGroupDashboardList"] !== null) {
            if (data["clusterGroupDashboardList"][dataCentre] !== undefined) {
                return data["clusterGroupDashboardList"][dataCentre]
            }
        }
        console.log("Could not find data for " + dataCentre)
        setError("No data for " + dataCentre);
        return {}
    }

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
                        setDashboard(getDashboard(data));
                        setIsLoading(false);
                    }
                }
            )
            .catch(e => console.log(e));

    }

    function displayEnvironments(headers, application) {
        application["environments"].map((environment, index) => {
            return (<td key={index}>{environment["podController"]["version"]}</td>);
        });
    }

    if (isLoading) {
        return (
            <div/>
        )
    } else if (error !== "") {
        return (
            <div>
                <h4>{error}</h4>
            </div>
        )
    } else {
        return (
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
        )
    }
}

export default TeamDashboard