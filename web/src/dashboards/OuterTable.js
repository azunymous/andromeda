import React, {useEffect, useState} from 'react';
import Table from "./Table";
import Config from "../Configuration";

function OuterTable(props) {
    let [isLoading, setIsLoading] = useState(true);
    let [team, setTeam] = useState(props["team"]);
    let [selectedDataCentre, setSelectedDataCentre] = useState("Kubernetes");
    let [dataCentres, setDataCentres] = useState([]);

    useEffect(fetchDataCentres, []);

    function getClusters(data) {
        let keys = [];
        for (let k in data["clusters"]) keys.push(k);

        return keys;
    }

    function fetchDataCentres() {
        if (props["dataCentre"] != null) {
            setSelectedDataCentre(props["dataCentre"]);
        }
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
                <ul>
                    {dataCentres.map((dataCentre, index) => {
                        return (
                            <li key={index}><a href={`/${team}/${dataCentre}`}>{dataCentre}</a></li>
                        );
                    })}
                </ul>
                <Table dataCentre={selectedDataCentre} team={team}/>
            </div>
        )
    }
}


export default OuterTable