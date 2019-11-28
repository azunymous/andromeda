import React, {useEffect, useState} from 'react';
import {A, useRoutes} from 'hookrouter';
import TeamDashboard from "./TeamDashboard";
import Config from "../Configuration";

function OuterTable(props) {
    let [isLoading, setIsLoading] = useState(true);
    let [team] = useState(props["team"]);
    let [dataCentres, setDataCentres] = useState([]);

    const routes = {
        '/': () => <TeamDashboard team={team} dataCentre="kubernetes" mode={"CONTROLLER"}/>,
        '/:dataCentre': ({dataCentre}) => <TeamDashboard team={team} dataCentre={dataCentre}/>,
    };
    const routeResult = useRoutes(routes);
    useEffect(fetchDataCentres, [props]);

    function getClusters(data) {
        let keys = [];
        for (let k in data["clusters"]) keys.push(k);

        return keys;
    }

    function fetchDataCentres() {
        if (dataCentres.length === 0) {
            console.log("Getting dataCentres")
            fetch(Config.getAPIURL() + '/config/')
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
                            setDataCentres(getClusters(data));
                            setIsLoading(false);
                            console.log(dataCentres)
                        }
                    }
                )
                .catch(e => console.log(e));
        }
    }

    if (isLoading) {
        return (
            <div>
                Loading...
            </div>
        )
    } else {
        return (
            <div>
                <span>
                <ul>
                    {dataCentres.map((dataCentre, index) => {
                        return (
                            <li key={index}><A href={`/${team}/${dataCentre}`}>{dataCentre}</A>
                            </li>
                        );
                    })}
                </ul>
                </span>
                {routeResult || "Error"}
            </div>
        )
    }
}

export default OuterTable