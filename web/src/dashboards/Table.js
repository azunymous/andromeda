import React, {useEffect, useState} from 'react'
import Config from "../Configuration";

function Table(props) {
    let [isLoading, setIsLoading] = useState(true);
    let [dashboard, setDashboard] = useState({});
    let [selectedTeam, setSelectedTeam] = useState(props["team"]);
    let [selectedDatacentre, setSelectedDataCentre] = useState(props["dataCentre"]);

    useEffect(fetchDashboard, []);

    function getDashboard(data) {
        if (data["clusterGroupDashboardList"] !== null) {
            if (data["clusterGroupDashboardList"][selectedDatacentre] !== null) {
                return data["clusterGroupDashboardList"][selectedDatacentre]
            } else {
                return {}
            }
        }
    }

    function fetchDashboard() {
        console.log("Getting dataCentres")
        fetch(Config.getAPIURL() + '/team/' + selectedTeam)
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
                        setDashboard(getDashboard(data));
                        setIsLoading(false);
                        console.log(data)
                    }
                }
            )
            .catch(e => console.log(e));

    }

    if (isLoading) {
        return (
            <div>
                {selectedTeam} + {selectedDatacentre}
            </div>
        )
    } else {
        return (
            <table>
                <th/>
                {dashboard["clusterGroupEnvironments"].map((environmentHeader, index) => {
                    return (
                        <th key={index}>{environmentHeader}</th>
                    )
                })}
            </table>
        )
    }
}

export default Table