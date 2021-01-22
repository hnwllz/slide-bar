<template>
     <div class="slide-verify" :style="widthlable"  onselectstart="return false;">
        <canvas :width="width" :height="canvasHeight" ref="canvas" ></canvas>
        <canvas :width="width" :height="canvasHeight" ref="block"  class="slide-verify-block"></canvas>
        <!-- container -->
        <div class="slide-verify-slider" :style="widthlable" :class="{'container-active': containerActive, 'container-success': containerSuccess, 'container-fail': containerFail}">
            <div class="slide-verify-slider-mask">
                <!-- slider -->
                <div @mousedown="sliderDown"
                    @touchstart="touchStartEvent"
                    @touchmove="touchMoveEvent"
                    @touchend="touchEndEvent"
                    class="slide-verify-slider-mask-item"
                    :style="{left: sliderLeft}">
                    <div class="slide-verify-slider-mask-item-icon"></div>
                </div>
            </div>
            <span class="slide-verify-slider-text">{{sliderText}}</span>
        </div>
    </div>
</template>

<script>
const SLIDER_ITEM_WIDTH = 60

function sum (x, y) {
  return x + y
}

function square (x) {
  return x * x
}

export default {
  name: 'SlideVerify',
  props: {
    // slider width
    width: {
      type: Number,
      default: 310
    },
    // canvas height
    canvasHeight: {
      type: Number,
      default: 155
    },
    blockY: {
      type: Number,
      default: 20
    },
    sliderText: {
      type: String,
      default: '向右滑动验证'
    },
    imgurl: {
      type: String,
      default: ''
    },
    miniimgurl: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      containerActive: false, // container active class
      containerSuccess: false, // container success class
      containerFail: false, // container fail class
      canvasCtx: null,
      blockCtx: null,
      block: null,
      canvasStr: null,
      img: undefined,
      originX: undefined,
      originY: undefined,
      isMouseDown: false,
      trail: [],
      widthlable: '',
      sliderLeft: 0, // block right offset
      sliderMaskWidth: 0 // mask width
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      this.initDom()
      this.bindEvents()
      this.widthlable = 'width:' + this.width + 'px;'
    },
    initDom () {
      this.block = this.$refs.block
      this.canvasStr = this.$refs.canvas

      this.canvasCtx = this.canvasStr.getContext('2d')
      this.blockCtx = this.block.getContext('2d')
    },
    initImg () {
      var that = this
      const img = document.createElement('img')
      img.onload = onload
      img.onerror = () => {
        img.src = that.imgurl
      }
      img.src = that.imgurl
      img.onload = function () {
        that.canvasCtx.drawImage(img, 0, 0, that.width, that.canvasHeight)
      }

      this.img = img
      const img1 = document.createElement('img')
      var blockCtx = that.blockCtx
      var blockY = that.blockY
      img1.onerror = () => {
        img1.src = that.miniimgurl
      }
      img1.src = that.miniimgurl
      img1.onload = function () {
        blockCtx.drawImage(img1, 0, blockY, img1.width, img1.height)
      }
    },
    sliderDown (event) {
      this.originX = event.clientX
      this.originY = event.clientY
      this.isMouseDown = true
    },
    touchStartEvent (e) {
      this.originX = e.changedTouches[0].pageX
      this.originY = e.changedTouches[0].pageY
      this.isMouseDown = true
    },
    bindEvents () {
      document.addEventListener('mousemove', (e) => {
        if (!this.isMouseDown) return false
        const moveX = e.clientX - this.originX
        const moveY = e.clientY - this.originY
        if (moveX < 0 || moveX + SLIDER_ITEM_WIDTH - 2 >= this.width) return false
        this.sliderLeft = moveX + 'px'
        const blockLeft = moveX
        this.block.style.left = blockLeft + 'px'

        this.containerActive = true // add active
        this.sliderMaskWidth = moveX + 'px'
        this.trail.push(moveY)
      })
      document.addEventListener('mouseup', (e) => {
        if (!this.isMouseDown) return false
        this.isMouseDown = false
        if (e.clientX === this.originX) return false
        this.containerActive = false // remove active
        this.verify()
      })
    },
    touchMoveEvent (e) {
      if (!this.isMouseDown) return false
      const moveX = e.changedTouches[0].pageX - this.originX
      const moveY = e.changedTouches[0].pageY - this.originY
      if (moveX < 0 || moveX + 38 >= this.width) return false
      this.sliderLeft = moveX + 'px'
      const blockLeft = (this.width - 40 - 20) / (this.width - 40) * moveX
      this.block.style.left = blockLeft + 'px'

      this.containerActive = true
      this.sliderMaskWidth = moveX + 'px'
      this.trail.push(moveY)
    },
    touchEndEvent (e) {
      if (!this.isMouseDown) return false
      this.isMouseDown = false
      if (e.changedTouches[0].pageX === this.originX) return false
      this.containerActive = false
      this.verify()
    },
    verify () {
      const arr = this.trail // drag y move distance
      const average = arr.reduce(sum) / arr.length // average
      const deviations = arr.map(x => x - average) // deviation array
      const stddev = Math.sqrt(deviations.map(square).reduce(sum) / arr.length) // standard deviation
      const left = parseInt(this.block.style.left)

      // 如果方差是0，则说明移动过程中完成没有抖动，可能是机器人
      if (stddev === 0) {
        this.reset()
        return
      }

      this.$emit('verify', left)
    },
    reset () {
      this.containerActive = false
      this.containerSuccess = false
      this.containerFail = false
      this.sliderLeft = 0
      this.block.style.left = 0
      this.sliderMaskWidth = 0
      this.canvasCtx.clearRect(0, 0, this.width, this.canvasHeight)
      this.blockCtx.clearRect(0, 0, this.width, this.canvasHeight)
      this.initImg()
    }
  }
}
</script>
<style scoped>
    .slide-verify {
        position: relative;
        width: 310px;
    }

    .slide-verify-block {
        position: absolute;
        left: 0;
        top: 0
    }

    .slide-verify-slider {
        position: relative;
        text-align: center;
        width: 310px;
        height: 40px;
        line-height: 40px;
        margin-top: 8px;
        background: #f7f9fa;
        color: #45494c;
        border: 1px solid #e4e7eb;
        border-radius: 200px;
    }

    .slide-verify-slider-mask {
        position: absolute;
        left: 0;
        top: 0;
        border: 0 solid #1991FA;
        background: #D1E9FE
    }

    .slide-verify-slider-mask-item {
        position: absolute;
        top: -8px;
        left: 0;
        width: 60px;
        height: 60px;
        cursor: pointer;
    }

    .container-active .slide-verify-slider-mask-item-icon {
        background-position: -3px 320px
    }

    .slide-verify-slider-mask-item-icon {
        position: absolute;
        left: 0;
        top: 0;
        width: inherit;
        height: inherit;
        background: url('../../../src/assets/slider_icon.1.2.4.png') -3px 392px;
    }

    .container-success .slide-verify-slider-mask-item {
        height: 38px;
        top: -1px;
        border: 1px solid #52CCBA;
        background-color: #52CCBA !important;
    }

    .container-success .slide-verify-slider-mask {
        height: 38px;
        border: 1px solid #52CCBA;
        background-color: #D2F4EF;
    }

    .container-success .slide-verify-slider-mask-item-icon {
        background-position: 0 0 !important;
    }

    .container-fail .slide-verify-slider-mask-item {
        height: 38px;
        top: -1px;
        border: 1px solid #f57a7a;
        background-color: #f57a7a !important;
    }

    .container-fail .slide-verify-slider-mask {
        height: 38px;
        border: 1px solid #f57a7a;
        background-color: #fce1e1;
    }

    .container-fail .slide-verify-slider-mask-item-icon {
        top: 14px;
        background-position: 0 -82px !important;
    }

    .container-active .slide-verify-slider-text,
    .container-success .slide-verify-slider-text,
    .container-fail .slide-verify-slider-text {
        display: none;
    }
</style>
