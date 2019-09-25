import React, {Fragment} from "react";
import "./Serviceklage.less"

export const Serviceklage = (props) => {
    return (
        <Fragment>
            <p><b>Dato opprettet:</b> {props.datoOpprettet.toString().slice(0, 10)}</p>
            <p><b>{props.paaVegneAv === "ORGANISASJON" ? "Organisasjonsnummer" : "Personnummer"}</b> {props.klagenGjelderId}</p>
            <p><b>Klagetype:</b> {props.klagetype}</p>
            <p><b>Klagetekst:</b> {props.klagetekst}</p>
            {props.erServiceklage &&
                <Fragment>
                    <p><b>Er klagen en serviceklage?:</b> {props.erServiceklage}</p>
                    {props.erServiceklage.includes("Ja") &&
                        <Fragment>
                            <p><b>Utfall:</b> {props.utfall}</p>
                            <p><b>Svarmetode:</b> {props.svarmetode}</p>
                        </Fragment>}
                    {props.erServiceklage.includes("Nei") &&
                        <Fragment>
                            <p><b>Gjelder:</b> {props.gjelder}</p>
                        </Fragment>}
                </Fragment>}
        </Fragment>

    )
}