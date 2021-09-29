import React, {Component} from "react";
import {Document, Page} from "react-pdf";
import Hovedknapp from "nav-frontend-knapper/lib/hovedknapp";
import "./Dokument.less";

export default class Dokument extends Component {
  state = {
    numPages: null,
    pageNumber: 1
  };

  onDocumentLoadSuccess = document => {
    const { numPages } = document;
    this.setState({
      numPages,
      pageNumber: 1
    });
  };

  changePage = offset =>
    this.setState(prevState => ({
      pageNumber: prevState.pageNumber + offset
    }));

  previousPage = () => this.changePage(-1);

  nextPage = () => this.changePage(1);

  render() {
    const { numPages, pageNumber } = this.state;

    const { pdf } = this.props;
    return (
      <>
          <div className="GosysButton">
              <Hovedknapp onClick={() => window.location = window.location.href.includes("preprod") ?
                  "https://gosys-q1.dev.intern.nav.no/gosys"
                  : "https://gosys.dev.intern.nav.no/gosys"}>
                  Tilbake til Gosys
              </Hovedknapp>
          </div>
        {pdf && (
          <>
            <Document file={pdf} onLoadSuccess={this.onDocumentLoadSuccess}>
              <Page pageNumber={pageNumber} />
            </Document>
              {numPages > 1 &&
              <div>
                <p>
                  Side {pageNumber || (numPages ? 1 : "--")} av {numPages || "--"}
                </p>
                <button
                  type="button"
                  disabled={pageNumber <= 1}
                  onClick={this.previousPage}
                >
                  Forrige
                </button>
                <button
                  type="button"
                  disabled={pageNumber >= numPages}
                  onClick={this.nextPage}
                >
                  Neste
                </button>
              </div>}
          </>
        )}
      </>
    );
  }
}
