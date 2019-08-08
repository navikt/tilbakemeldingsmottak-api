module.exports = {
    verbose: true,
    testEnvironment: 'node',
    transformIgnorePatterns: ["<rootDir>/node_modules/"],
    moduleNameMapper: {'~/(.*)$': '<rootDir>/src/$1', "^.+\\.worker.js": "<rootDir>/testMocks/workerMock.js",},
    transform: {
        ".*\\.(less|svg)$": "<rootDir>/testMocks/styleMock.js",
        "^.+\\.js$": "babel-jest",
    },
    setupFiles: [
        "./testMocks/setupJest.js"
    ]
}