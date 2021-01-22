
import Vue from 'vue'
import popVerifySliderVue from './popverifyslider.vue'

const PopVerifySliderConstructor = Vue.extend(popVerifySliderVue)

PopVerifySliderConstructor.prototype.close = function () {
  if (this.$el && this.$el.parentNode) {
    this.$el.parentNode.removeChild(this.$el)
  }

  this.$destroy()
}

const popVerifySlider = (options) => {
  options = options || {}
  const instance = new PopVerifySliderConstructor({
    el: document.createElement('div')
  })

  instance.onSuccess = options.onSuccess

  document.body.appendChild(instance.$el)
  window.app = instance
  return instance
}

export default popVerifySlider
