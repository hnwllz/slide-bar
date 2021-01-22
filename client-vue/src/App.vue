<template>
  <div id="app">
      <button @click="show">验证</button>
  </div>
</template>

<script>
import PopVerifySlider from '../packages/verifyslider'
import Vue from 'vue'
Vue.use(PopVerifySlider)

export default {
  name: 'App',
  data () {
    return {
    }
  },
  components: {
  },
  methods: {
    show () {
      this.$popverifyslider({
        onSuccess (args) {
          console.log('校验成功:' + args.checkMoveid)
        }
      })
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
