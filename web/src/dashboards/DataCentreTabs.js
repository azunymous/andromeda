import React, {useEffect, useState} from 'react';
import Config from "../options/Configuration";
import {Nav, NavItem, NavLink} from "reactstrap";
import {useQueryParams} from "hookrouter";

function DataCentreTabs({team}) {
    const [queryParams, setQueryParams] = useQueryParams();
    const {
        clustergroup = "kubernetes",
    } = queryParams;

    const clusterHandler = (input) => {
        setQueryParams({clustergroup: input});
    };

    let [isLoading, setIsLoading] = useState(true);
    let [dataCentres, setDataCentres] = useState([]);

    useEffect(fetchDataCentres, [team]);

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
            <Nav tabs>
                <NavItem>
                    <NavLink href="#" disabled/>
                </NavItem>
            </Nav>
        )
    } else {
        return (
            <Nav tabs>
                {dataCentres.map((dataCentre, index) => {
                    return (
                        <NavItem key={index}>
                            <NavLink onClick={() => clusterHandler(dataCentre)}
                                     active={dataCentre === clustergroup}>{dataCentre}</NavLink>
                        </NavItem>
                    );
                })}
            </Nav>
        )
    }
}

export default DataCentreTabs