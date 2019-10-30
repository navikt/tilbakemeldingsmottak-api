import React, { Component } from "react";
import { Document, Page } from "react-pdf";

import samplePDF from "../test.pdf";
console.log(samplePDF)

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
            <div>
              <p>
                Page {pageNumber || (numPages ? 1 : "--")} of {numPages || "--"}
              </p>
              <button
                type="button"
                disabled={pageNumber <= 1}
                onClick={this.previousPage}
              >
                Previous
              </button>
              <button
                type="button"
                disabled={pageNumber >= numPages}
                onClick={this.nextPage}
              >
                Next
              </button>
            </div>
          </>
        )}
      </>
    );
  }
}
