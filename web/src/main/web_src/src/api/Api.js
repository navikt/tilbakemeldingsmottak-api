import axios from 'axios'
import uuidv4 from 'uuid'
import config from "../config"

const API_ROOT = config.services.serviceklageBackend;

const instance = axios.create({
    withCredentials: true,
    headers: {'Content-Type': 'application/json', 'Accept': 'application/json', "callId": uuidv4()}
});

const requests = {
    delete: (url, data, headers) => instance.delete(`${API_ROOT}${url}`, {data: data, headers: headers}).catch(err=>{throw err.response}),
    get: (url, config) => instance.get(`${API_ROOT}${url}`, config).catch(err=>{throw err.response}),
    post: (url, data, headers) => instance.post(`${API_ROOT}${url}`, data, {headers:headers}).catch(err=>{throw err.response}),
    put: (url, data, headers) => instance.put(`${API_ROOT}${url}`, data, {headers: headers}).catch(err=>{throw err.response})
}


export const ServiceklageApi = {
    klassifiserServiceklage: (journalpostId, oppgaveId, payload) => requests.put(`/klassifiser?journalpostId=${journalpostId}&oppgaveId=${oppgaveId}`, payload),
    hentServiceklage: (journalpostId) => requests.get(`/${journalpostId}`)
};