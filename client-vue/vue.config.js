console.log('has invoked')

module.exports = {
  devServer: {
    hot: true,
    overlay: {
      warnings: false,
      errors: false
    },
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080/',
        ws: false,
        changeOrigin: true,
        pathRewrite: { '^/api': '/SpringRest' }
      }
    }
  }
}
