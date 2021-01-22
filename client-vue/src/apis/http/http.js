import axios from './axios'

export default {
  /**
     * get 请求
     * @param url 接口路由
     * @param auth 是否需要带登录信息
     * @returns {AxiosPromise<any>}
     */
  get (url, auth = false) {
    if (auth) {
      return axios.get(url, { headers: { Authorization: 'Your back-end user authenticates information' } }).then(
        res => {
          if (res) {
            return Promise.resolve(res.data)
          }

          return Promise.reject(res)
        })
    } else {
      return axios.get(url).then(
        res => {
          if (res) {
            return Promise.resolve(res.data)
          }

          return Promise.reject(res)
        })
    }
  },

  /**
     * post 请求
     *
     * @param url 接口路由
     * @param data 接口参数
     * @param auth 是否需要带登录信息
     * @returns {AxiosPromise<any>}
     */
  post (url, data, auth = false) {
    if (auth) {
      return axios.post(url, data, { headers: { Authorization: 'Your back-end user authenticates information' } }).then(
        res => {
          if (res) {
            Promise.resolve(res.data)
            return
          }

          Promise.reject(res)
        })
    } else {
      return axios.post(url, data).then(
        res => {
          if (res) {
            return Promise.resolve(res.data)
          }

          return Promise.reject(res)
        })
    }
  },

  /**
     * put请求
     * @param url 接口路由
     * @param data 接口参数
     * @param auth 是否需要带登录信息
     * @returns {AxiosPromise<any>}
     */
  put (url, data, auth = false) {
    if (auth) {
      return axios.put(url, data, { headers: { Authorization: 'Your back-end user authenticates information' } }).then(
        res => {
          if (res) {
            return Promise.resolve(res.data)
          }

          return Promise.reject(res)
        })
    } else {
      return axios.put(url, data).then(
        res => {
          if (res) {
            return Promise.resolve(res.data)
          }

          return Promise.reject(res)
        })
    }
  },

  /**
     * 删除
     * @param url 接口路由
     * @param auth 是否需要带登录信息
     * @returns {AxiosPromise}
     */
  del (url, auth = false) {
    if (auth) {
      return axios.delete(url, { headers: { Authorization: 'Your back-end user authenticates information' } }).then(
        res => {
          if (res) {
            return Promise.resolve(res.data)
          }

          return Promise.reject(res)
        })
    } else {
      return axios.delete(url).then(
        res => {
          if (res) {
            return Promise.resolve(res.data)
          }

          return Promise.reject(res)
        })
    }
  },

  /**
     * 上传文件
     * @param url 接口路由
     * @param file 接口文件
     * @param auth 是否需要带登录信息
     */
  uploader (url, file, auth = false) {
    let onProcess = null
    const tmp = auth
    if (typeof (tmp) === 'object') {
      onProcess = tmp.onProcess
      auth = tmp.auth || false
    }

    const param = new FormData()
    param.append('file', file)

    const config = {
      // 添加请求头
      headers: { 'Content-Type': 'multipart/form-data' },
      // 添加上传进度监听事件
      onUploadProgress: e => {
        //   var completeProgress = ((e.loaded / e.total * 100) | 0) + "%";
        onProcess(e)
      }
    }

    if (auth) {
      return axios.post(url, param, { headers: { Authorization: 'Your back-end user authenticates information' } })
    } else {
      return axios.post(url, param, config)
    }
  }
}
