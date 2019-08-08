const fs = require('fs-extra');
const path = require('path');

const source = './src/assets';
const destination = path.join(__dirname, '../../../resources/public');

console.log('Copying public files to ' + destination + ' ...');
fs.copy(source, destination, error => {
    if (error) return console.log(error);
    console.log('Done - Files successfully copied');
});