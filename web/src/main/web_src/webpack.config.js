const path = require('path');
const webpack = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const pkg = require('./package.json');
const OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin')
const CompressionPlugin = require('compression-webpack-plugin');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const fs = require("fs");

// Buildtype
const TARGET = process.env.npm_lifecycle_event;

// Set env variable
process.env.NODE_ENV = process.env.NODE_ENV || 'development';
const devMode = process.env.NODE_ENV !== 'production';

const outputDir = {
    development: 'dist/devOutput',
    production: './../resources/public'
};

const statsOutputSettings = {
    colors: true,
    chunks: false,
    modules: false
};

const webpackConfig = {
    mode: process.env.NODE_ENV,
    devtool: 'source-map',
    entry: ['@babel/polyfill', 'react-hot-loader', './src/index.js'],
    performance: {hints: false},
    output: {
        filename: 'bundle.js',
        publicPath: '/',
        path: path.join(__dirname, 'dist'),
        globalObject: 'this'
    },
    stats: "minimal",
    devServer: {
        stats: statsOutputSettings,
        contentBase: path.join(__dirname, 'public'),
        historyApiFallback: true,
        hot: true,
        proxy: {
            '/rest/serviceklage': {
                target: 'http://localhost:8080'
            }
        }
    },
    plugins: [
        new webpack.DefinePlugin({
            'process.env': {
                NODE_ENV: JSON.stringify(process.env.NODE_ENV) || '"development"',
                CLIENT_VERSION: JSON.stringify(pkg.version) || '""'
            }
        }),
        new MiniCssExtractPlugin({
            // Options similar to the same options in webpackOptions.output
            // both options are optional
            filename: devMode ? '[name].css' : '[name].[hash].css'
        }),
        new webpack.HotModuleReplacementPlugin(),
        new HtmlWebpackPlugin({
            title: 'Serviceklage',
            //favicon: 'src/assets/favicon.ico',
            inject: false,
            template: require('html-webpack-template'),
            appMountId: 'root'
        })
    ],
    resolve: {
        alias: {
            '~': path.resolve(__dirname, 'src'),
            lessVars: path.resolve(__dirname, 'src/styles/variables.less')
        },
        extensions: ['*', '.mjs', '.js', '.jsx', '.vue', '.json', '.gql', '.graphql']
    },
    module: {
        rules: [
            {
                test: /\.mjs$/,
                include: /node_modules/,
                type: 'javascript/auto'
            },
            {
                test: /\.ts(x?)$/,
                exclude: /node_modules/,
                use: ['babel-loader', 'ts-loader']
            },
            {
                test: /\.js$/,
                exclude: /node_modules/,
                use: ['babel-loader']
            },
            {
                test: /\.less$/,
                use: [
                    devMode ? 'style-loader' : MiniCssExtractPlugin.loader,
                    'css-loader',
                    'less-loader?{"globalVars":{"nodeModulesPath":"\'~\'", "coreModulePath":"\'~\'"}}'
                ]
            },
            {
                test:/\.(graphql|gql)$/,
                exclude: /node_modules/,
                loader: 'graphql-tag/loader'
            },
            {
                test: /\.css$/,
                use: [devMode ? 'style-loader' : MiniCssExtractPlugin.loader, 'css-loader']
            },
            {
                // images
                test: /\.(ico|jpe?g|png|gif|woff|woff2|eot|ttf|svg)$/,
                use: ['file-loader']
            }
        ]
    }
};

if (process.env.WEB_COMPONENT === "true") {
    webpackConfig.output = {
        ...webpackConfig.output,
        library: 'serviceklage',
        libraryTarget: 'window',
    }
}

// If dev build
if (TARGET === 'build-dev') {
    webpackConfig.output = {
        ...webpackConfig.output,
        path: path.join(__dirname, outputDir.development),
        filename: 'bundle.js',
        publicPath: '/',
        globalObject: 'this'
    };
}

// If production build
if (TARGET === 'build') {
    webpackConfig.devtool = 'none';
    webpackConfig.output = {
        library: 'serviceklage',
        libraryTarget: 'window',
        path: path.join(__dirname, outputDir.production),
        filename: 'bundle.js',
        publicPath: '/',
        globalObject: 'this'
    }
    webpackConfig.plugins = webpackConfig.plugins.concat([
        new UglifyJsPlugin({
            parallel: true
        }),
        new CompressionPlugin(),
        //new CleanWebpackPlugin(['dist']),
        new OptimizeCssAssetsPlugin({
            assetNameRegExp: /\.css$/g,
            cssProcessor: require('cssnano'),
            cssProcessorPluginOptions: {
                preset: ['default', {discardComments: {removeAll: true}}]
            },
            canPrint: true
        }),
        {
            apply: compiler => compiler.hooks.done.tap("ServiceklageHtmlPlugin", params => {
                let outputPath = path.join(params.compilation.outputOptions.path, "index.html");
                let html = fs.readFileSync(outputPath).toString("utf-8");
                let serviceklagescript =
                    "<script>" +
                    "window.serviceklage.render(document.getElementById('root'))" +
                    "</script>";
                let serviceKlageHtml = html.replace(/<\/body>/, serviceklagescript + "</body>")
                fs.writeFileSync(outputPath, serviceKlageHtml);
            })
        }
    ])
}

module.exports = webpackConfig;
