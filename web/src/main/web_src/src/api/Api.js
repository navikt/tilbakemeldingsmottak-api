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
          console.log('Delete result: ' + err.response.statusText);
          console.log('Delete response header: '+ err.response.headers);
          throw err.response;
      }),
  get: (url, config) =>
    instance.get(`${API_ROOT}${url}`, config).catch(err => {
        console.log('Get result: ' + err.response.statusText);
        console.log('Get response header: '+ err.response.headers);
        throw err.response;
    }),
  post: (url, data, headers) =>
    instance
      .post(`${API_ROOT}${url}`, data, { headers: headers })
      .catch(err => {
          console.log('Post result: ' + err.response.statusText);
          console.log('Post response header: '+ err.response.headers);
          throw err.response;
      }),
  put: (url, data, headers) =>
    instance.put(`${API_ROOT}${url}`, data, { headers: headers }).catch(err => {
        console.log('Put result: ' + err.response.statusText);
        console.log('Put response header: '+ err.response.headers);
        throw err.response;
    })
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
    if (401 === error.response.status || 403 === error.response.status) {
        alert("Autentisering mangler, du må logge inn for å fortsette til " +  window.location.href);

        window.location = window.location.href.includes("preprod") ?
            "https://loginservice.nais.preprod.local/login?redirect=https://tilbakemeldingsmottak-q1.nais.preprod.local/login"+"?redirect_cookie="+window.location.href :
            "https://loginservice.nais.local/login?redirect=https://tilbakemeldingsmottak.nais.adeo.no/login"+"?redirect_cookie="+window.location.href;
    } else {
        return Promise.reject(error);
    }
}, null, { synchronous: true });