
module.exports = {
    mode: 'production',
    entry: './src/index.js',
    experiments: { outputModule: true },
    output: {
        path: __dirname + '/dist',
        filename: 'test.[name].js',
        library: { type: 'module' }
    },
    module: {
        rules: [
            { test: /\.js$/, use: 'babel-loader' },
        ]
    },
    stats: {
        colors: true
    },
    target: "web",
    externals: /k6(\/.*)?/,
    devtool: 'source-map',
}