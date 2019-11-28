import React, {useEffect, useState} from 'react'
import Config from "../Configuration";
import {Table} from "reactstrap";
import Application from "../row/Application";
import {useQueryParams} from "hookrouter";

function TeamDashboard({team, dataCentre}) {
    const [queryParams, setQueryParams] = useQueryParams();
    const {
        mode = 'CONTROLLER'
    } = queryParams;

    let [isLoading, setIsLoading] = useState(true);
    let [dashboard, setDashboard] = useState({});
    let [error, setError] = useState("");

    useEffect(fetchDashboard, []);
    const viewHandler = (view) => {
        setQueryParams({mode: view});
    };

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
            <div>
                <span>
                    <ul>
                        <li>
                              <a onClick={() => viewHandler("CONTROLLER")}>CONTROLLERS</a>
                        </li>
                        <li>
                                <a onClick={() => viewHandler("POD")}>PODS</a>
                        </li>
                    </ul>
                </span>
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