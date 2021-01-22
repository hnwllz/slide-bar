<template>
    <div class="pop-verify-slider">
      <div class="pop-verify-slider-content">
          <Base ref="base"
            :width="260"
            :canvasHeight="160"
            :blockY="blockY"
            :imgurl="imgurl"
            :miniimgurl="miniimgurl"
            @verify="onVerify"
            :slider-text="text" />
            <div>
              <div class="popverify-small">
                <a class="popverify-close" @click="closeSlider"></a>
                <a class="popverify-refresh" @click="refresh"></a>
              </div>
            </div>
      </div>
    </div>
</template>
<script>
import Base from './main.vue'
import LoginApi from '../../../src/apis/loginApi'

export default {
  name: 'PopVerifySlider',
  props:
    {
      maxFailTimes: {
        type: Number,
        default: 3
      },
      onSuccess: {
        type: Function,
        default () {}
      }
    },
  data () {
    return {
      blockY: 0,
      imgurl: '',
      miniimgurl: '',
      text: '滑动验证',
      failTimes: 0
    }
  },
  methods: {
    initBaseSlider () {
      this.failTimes = 0

      LoginApi.getImageVerifyCode().then(res => {
        if (!res || res.code !== 200) {
          this.$emit('fail', '初始化图形验证码失败')
        } else {
          var imgobj = JSON.parse(res.data)
          this.blockY = imgobj.yHeight
          this.imgurl = 'data:image/png;base64,' + imgobj.bigImage
          this.miniimgurl = 'data:image/png;base64,' + imgobj.smallImage
          this.chenckMoveid = imgobj.chenckMoveid
          if (this.$refs.base) {
            this.$nextTick(() => {
              this.$refs.base.reset()
            })
          }
        }
      })
    },
    onVerify (offsetX) {
      if (!offsetX) {
        return
      }

      LoginApi.checkVerifyData(this.chenckMoveid, offsetX).then(
        res => {
          if (res && res.code === 200) {
            this.onSuccess && this.onSuccess({
              checkMoveid: this.chenckMoveid
            })

            this.close()
          } else {
            if (!this.maxFailTimes || ++this.failTimes < this.maxFailTimes) {
              this.$refs.base.reset()
              return
            }
            // 超过最大限制失败次数，重新请求验证码
            this.initBaseSlider()
          }
        }
      )
    },
    refresh () {
      this.initBaseSlider()
    },
    closeSlider () {
      this.close()
    }
  },
  components: {
    Base
  },
  mounted () {
    this.initBaseSlider()
  }
}
</script>

<style lang="scss" scoped>
.pop-verify-slider{
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;

  .pop-verify-slider-content{
    position: absolute;
    left: 50%;
    top: 50%;
    width: 282px;
    padding: 10px;
    box-sizing: border-box;
    transform: translate(-50%, -50%);
    border: 1px solid #ccc;
  }

  .popverify-small{
    text-align: left;
    margin-top: 18px;

    a.popverify-close, a.popverify-refresh{
      cursor: pointer;
      display: inline-block;
      width: 20px;
      height: 20px;
      margin-left: 8px;
      background: url("../../../src/assets/slider_icon.1.2.4.png") no-repeat;
      background-size: 1200%;
    }
    a.popverify-close{
      margin: 0;
      background-position: 2px -172px;

      &:hover{
        background-position: 2px -196px;
      }
    }
    a.popverify-refresh{
        background-position: 2px -314px;

        &:hover{
          background-position: 2px -338px;
        }
    }
  }
}
</style>
