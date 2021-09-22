import axios from 'axios';
import uuidv4 from 'uuid';
import config from '../config';

const API_ROOT = config.services.serviceklageBackend;

const callId = uuidv4();

const instance = axios.create({
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
    callId,
  },
});

const checkAuthentication = async () => {
  if (!ServiceklageApi.isAuthenticated) {
    const response = await requests
      .get(`/autentisert`, undefined, true)
      .catch((err) => {
        console.log(err.status);
        if (err.status === 401) {
          ServiceklageApi.isAuthenticated = false;
          redirectToLogin();
          return;
        }
        throw err;
      });

    if (response && response.status === 200) {
      ServiceklageApi.isAuthenticated = true;
    }
  }
  return ServiceklageApi.isAuthenticated;
};

const requests = {
  delete: async (url, data, headers, skipAuth = false) => {
    if (!skipAuth && !(await checkAuthentication())) return;

    return await instance
      .delete(`${API_ROOT}${url}`, { data: data, headers: headers })
      .catch((err) => {
        console.log('Delete result: ' + err.response.statusText);
        console.log('Delete response header: ' + err.response.headers);
        throw err.response;
      });
  },
  get: async (url, config, skipAuth = false) => {
    if (!skipAuth && !(await checkAuthentication())) return;

    return await instance.get(`${API_ROOT}${url}`, config).catch((err) => {
      console.log('Get result: ' + err.response.statusText);
      console.log('Get response header: ' + err.response.headers);
      throw err.response;
    });
  },
  post: async (url, data, headers, skipAuth = false) => {
    if (!skipAuth && !(await checkAuthentication())) return;

    return await instance
      .post(`${API_ROOT}${url}`, data, { headers: headers })
      .catch((err) => {
        console.log('Post result: ' + err.response.statusText);
        console.log('Post response header: ' + err.response.headers);
        throw err.response;
      });
  },
  put: async (url, data, headers, skipAuth = false) => {
    if (!skipAuth && !(await checkAuthentication())) return;

    return await instance
      .put(`${API_ROOT}${url}`, data, { headers: headers })
      .catch((err) => {
        console.log('Put result: ' + err.response.statusText);
        console.log('Put response header: ' + err.response.headers);
        throw err.response;
      });
  },
};

export const ServiceklageApi = {
  isAuthenticated: false,
  klassifiser: (oppgaveId, payload) =>
    requests.put(`/klassifiser?oppgaveId=${oppgaveId}`, payload),
  hentKlassifiseringSkjema: (oppgaveId) =>
    requests.get(`/hentskjema/${oppgaveId}`),
  hentDokument: (oppgaveId) => requests.get(`/hentdokument/${oppgaveId}`),
};

const redirectToLogin = () => {
  window.location = window.location.href.includes('preprod')
    ? 'https://loginservice.nais.preprod.local/login?redirect=https://tilbakemeldingsmottak-q1.nais.preprod.local/login' +
      '?redirect_cookie=' +
      window.location.href
    : 'https://loginservice.nais.adeo.no/login?redirect=https://tilbakemeldingsmottak.nais.adeo.no/login' +
      '?redirect_cookie=' +
      window.location.href;
};

instance.interceptors.response.use(
  function (response) {
      if (response.status === 204) {
          return Promise.reject('Det er ikke mulig å behandle en serviceklage når det ikke er et dokument tilknyttet oppgaven.');
      }
    return response;
  },
  function (error) {
    if (401 === error.response.status || 403 === error.response.status) {
      redirectToLogin();
    } else {
      return Promise.reject(error);
    }
  },
  null,
  { synchronous: true }
);
