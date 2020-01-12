import React, {useState} from 'react'
import {Badge, Button,  Modal, ModalHeader, ModalBody, ModalFooter} from "reactstrap";

import canaryImage from "./canary.svg"

function Application({application, headers, mode}) {
    return (
        <tr>
            <th scope="row">{application["name"]}</th>
            {application["environments"].map((environment, index) => {
                return <Environment key={index} index={index} environment={environment} headers={headers}
                                    mode={mode}/>
            })}
        </tr>
    )
}

function Environment({index, environment, headers, mode}) {
    const [modal, setModal] = useState(false);

    const toggle = () => setModal(!modal);

    if (mode === "CONTROLLER" && environment["environmentName"] === headers[index]) {
        return (
            <td className={environment["podController"]["status"]}>
                <Button onClick={toggle} size="lg" block
                        color={colorFrom(environment["podController"]["status"])}> {environment["podController"]["version"]}
                    {showCanary(environment["canaryPodController"])}
                </Button>
                <NamespaceInfo modal={modal} toggle={toggle} ingresses={environment["ingresses"]} namespaceName={environment["namespaceName"]}/>
            </td>
        )
    } else if ((mode === "POD" || mode === "DEPENDENCY") && environment["environmentName"] === headers[index]) {
        return (
            <td>
                <Pods pods={environment["podController"]["pods"]} dependencies={mode === "DEPENDENCY"}/>
                {showCanaryPods(environment["canaryPodController"], mode === "DEPENDENCY")}
            </td>
        )
    }

    return (
        <td className={"EMPTY"}/>
    )
}

function NamespaceInfo({modal, toggle, namespaceName, ingresses}) {
    return (
        <Modal isOpen={modal} toggle={toggle} className={"namespaceInfo"}>
            <ModalHeader toggle={toggle}>{namespaceName}</ModalHeader>
            <ModalBody>
                <h6>Ingresses:</h6>
                {ingresses.map((ingress, index) => {
                    return (
                        <div key={index}>
                            <code>{ingress}</code>
                        </div>
                    )
                })}
              </ModalBody>
        </Modal>
    )
}

function showCanary(canary) {
    if (canary["name"] === "") {
        return <span className={"noCanary"}/>
    }
    return <div>
        <span className={"canaryIconCircle bg-" + colorFrom(canary["status"])}><img className={"canaryIcon"}
                                                                                    src={canaryImage} alt="canary - "/></span>
        <span className="canaryVersion">{canary["version"]}</span>
    </div>
}

function showCanaryPods(canary, dependencies) {
    if (canary["name"] === "") {
        return <span className={"noCanary"}/>
    }
    return <Pods pods={canary["pods"]} dependencies={dependencies} canary/>

}

function Pods({pods, dependencies, canary}) {
    function showCanaryImage(canary) {
        if (canary) {
            return (
                <img className={"canaryIconPod"} src={canaryImage} alt="canary - "/>
            )
        }
    }

    return (
        pods.map((pod, index) => {
            return (
                <span key={index} className={"pod"}>
                <Button color={colorFrom(pod["status"])} className={pod["status"] + " " + pod["name"]}>
                    {showCanaryImage(canary)}<span className={"versionPod"}>{pod["version"]}</span>
                    <Dependencies enabled={dependencies} dependencies={pod["dependencies"]}/>
                    <FeatureFlags enabled={dependencies} featureFlags={pod["featureFlags"]}/>
                </Button>
                </span>
            );
        }))
}

function Dependencies({enabled, dependencies}) {
    if (enabled && dependencies !== undefined) {
        return (
            <div>
                {dependencies.map((dependency, index) => {
                    return <Badge key={index} className={"dependency "} color={colorFromGauge(dependency["up"])}
                                  pill>{dependency["name"]}</Badge>
                })}
            </div>
        )
    }
    return (<span/>)
}

function FeatureFlags({enabled, featureFlags}) {
    if (enabled && featureFlags !== undefined) {
        return (
            <div>
                {featureFlags.map((featureFlag, index) => {
                    return <span key={index}>
                        <Badge className={"featureFlag "}
                               color={colorFromStrategy(featureFlag["strategy"])}> {featureFlag["name"]}</Badge>
                    </span>
                })}
            </div>
        )
    }
    return (<span/>)
}

function colorFrom(status) {
    if (status === "READY") {
        return "success"
    } else if (status === "LIVE" || status === "SCALED_DOWN") {
        return "warning"
    } else if (status === "UNAVAILABLE") {
        return "danger"
    } else {
        return "secondary"
    }
}

function colorFromGauge(up) {
    if (up) {
        return "success"
    } else {
        return "danger"
    }
}

// This must be in float format due to the way the API parses the prometheus float into a string.
function colorFromStrategy(strategy) {
    if (strategy === "0.0") {
        return "secondary"
    } else if (strategy === "1.0") {
        return "info"
    } else if (strategy === "2.0") {
        return "dark"
    } else {
        return "warning"
    }
}

export default Application