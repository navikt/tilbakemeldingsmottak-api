import axios from "axios";
import uuidv4 from "uuid";
import config from "../config";

const API_ROOT = config.services.serviceklageBackend;

const callId = uuidv4()

const instance = axios.create({
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
    callId
  }
});

const requests = {
  delete: (url, data, headers) =>
    instance
      .delete(`${API_ROOT}${url}`, { data: data, headers: headers })
      .catch(err => {
        throw err.response;
      }),
  get: (url, config) =>
    instance.get(`${API_ROOT}${url}`, config).catch(err => {
      throw err.response;
    }) .then(console.log('Get result: ' + response.statusText, console.log('Get response header: '+ response.headers))),
  post: (url, data, headers) =>
    instance
      .post(`${API_ROOT}${url}`, data, { headers: headers })
      .catch(err => {
        throw err.response;
      }) .then(console.log('Post result: ' + response.statusText, console.log('Post response header: '+ response.headers))),
  put: (url, data, headers) =>
    instance.put(`${API_ROOT}${url}`, data, { headers: headers }).catch(err => {
      throw err.response;
    }) .then(console.log('Put result: ' + response.statusText, console.log('Put response header: '+ response.headers)))
};

export const ServiceklageApi = {
  klassifiser: (oppgaveId, payload) =>
    requests.put(
      `/klassifiser?oppgaveId=${oppgaveId}`,
      payload
    ),
  hentKlassifiseringSkjema: oppgaveId =>
    requests.get(`/hentskjema/${oppgaveId}`),
  hentDokument: oppgaveId => requests.get(`/hentdokument/${oppgaveId}`),
};

instance.interceptors.response.use(function (response) {
    return response;
}, function (error) {
    if (401 === error.response.status ) {
        alert({message: "Autentisering mangler, du må logge inn for å fortsette"}),
        function(){
            window.location.href = 'https://loginservice.dev.nav.no/login' + '?redirect=' + window.location.origin
            //window.location = 'https://loginservice.nais.preprod.local/login?redirect=https://tilbakemeldingsmottak-q1.nais.preprod.local/login';
            //return Promise.reject(error);
        };
    } else {
        return Promise.reject(error);
    }
}, null, { synchronous: true });