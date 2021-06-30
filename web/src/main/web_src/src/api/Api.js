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
    }),
  post: (url, data, headers) =>
    instance
      .post(`${API_ROOT}${url}`, data, { headers: headers })
      .catch(err => {
        throw err.response;
      }),
  put: (url, data, headers) =>
    instance.put(`${API_ROOT}${url}`, data, { headers: headers }).catch(err => {
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

window.axios.interceptors.response.use(function (response) {
    return response;
}, function (error) {
    if (401 === error.response.status) {
        swal({
            title: "Session Expired",
            text: "Your session has expired. Would you like to be redirected to the login page?",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "Yes",
            closeOnConfirm: false
        }, function(){
            window.location = 'https://loginservice.nais.preprod.local/login?redirect=https://tilbakemeldingsmottak-q1.nais.preprod.local/login';
            return Promise.reject(error);
        });
    } else {
        return Promise.reject(error);
    }
});