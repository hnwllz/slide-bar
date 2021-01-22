
import service from './src'

export default {
  install (Vue) {
    Vue.prototype.$popverifyslider = service
  }
}
