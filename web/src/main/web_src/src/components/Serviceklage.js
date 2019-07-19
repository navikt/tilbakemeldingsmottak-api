import React, {Fragment} from "react";
import "./Serviceklage.less"

export const Serviceklage = (props) => {
    return (
        <Fragment>
            <p><b>ServiceklageId:</b> {props.serviceklageId}</p>
            <p><b>Dato opprettet:</b> {props.datoOpprettet}</p>
            <p><b>Klagetype:</b> {props.klagetype}</p>
            <p><b>Klagetekst:</b> {props.klagetekst}</p>
            <p><b>Ønsker å kontaktes:</b> {props.oenskerAaKontaktes}</p>
            <p><b>Er behandlet:</b> {props.erBehandlet}</p>
        </Fragment>

    )
}