import http from './http/http'

const LoginApiUrls = {
  getImageVerifyCode: 'api/getImageVerifyCode',
  checkVerifyData: 'api/verifydata'
}

export default {
  getImageVerifyCode () {
    return http.post(LoginApiUrls.getImageVerifyCode)
  },
  checkVerifyData (verifyId, offsetX) {
    return http.post(LoginApiUrls.checkVerifyData, {
      x_index: offsetX,
      chenckMoveid: verifyId
    })
  }
}
