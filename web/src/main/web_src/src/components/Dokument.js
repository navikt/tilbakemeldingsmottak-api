import React, {Component} from "react";
import {Document, Page} from "react-pdf";

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
        {pdf && (
          <>
            <Document file={pdf} onLoadSuccess={this.onDocumentLoadSuccess}>
              <Page pageNumber={pageNumber} />
            </Document>
            numPages > 1 &&
              {<div>
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
