<template>
  <div class="side-bar-wrapper">
    <VueResizable
        class="resizable-side-bar"
        :width="500"
        :min-width="500"
        :max-width="Infinity"
        :active="['r']"
        v-if="isVisibleSideBar"
    >
      <div class="side-bar">
        <div class="title-area">
          <BInput placeholder="맛집 이름을 입력해주세요."/>
        </div>
        <div class="image-area">
          <div class="iw-file-input">
            사진을 업로드 해주세요
          </div>
        </div>
        <div class="location-info-area">
          <FontAwesomeIcon icon="location-dot" />
          <BInput
              placeholder="위치 정보 직접 입력하기"
              v-model="address"
          />
        </div>
        <div class="rate-area">
          <BFormRating />
        </div>
        <div class="review-area">
          <BFormTextarea
              ref="textarea"
              placeholder="후기를 입력해주세요."
          />
        </div>
      </div>
    </VueResizable>
    <BButton
        class="side-bar-active-btn"
        size="sm"
        @click="showSideBar"
    >
      <FontAwesomeIcon :icon="isVisibleSideBar ? 'angle-left' : 'angle-right'" />
    </BButton>
  </div>
</template>

<script>
import VueResizable from 'vue-resizable';

export default {
  name: 'SideBar',
  components: {
    VueResizable
  },
  data() {
    return {
      isVisibleSideBar: true,
      address: undefined,
    }
  },
  created() {
    this.$root.$refs.sideBar = this;
  },
  methods: {
    showSideBar() {
      this.isVisibleSideBar = !this.isVisibleSideBar;
    },
  }
}
</script>

<style lang="scss" scoped>

.side-bar-wrapper {
  display: flex;
  color: #fff;

  > .resizable-side-bar {
    > .side-bar {
      background-color: rgba(0, 0, 0, 0.5);
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      padding: 10px;

      > .title-area {
        padding: 20px 10px;

        input, input::placeholder, input:focus {
          font-family: 'Nanum Square', serif;
          font-size: 2rem;
          font-weight: bold;
          color: #fff;
          box-shadow: none;
          background: none;
          border: none;
        }
      }

      > .image-area {
        padding: 0 10px;

        > .iw-file-input {
          display: flex;
          justify-content: center;
          align-items: center;
          font-size: 1.3rem;
          border: 5px dashed rgb(255, 255, 255, 0.5);
          border-radius: 10px;
          height: 250px;
          background-color: rgb(255, 255, 255, 0.5);
        }
      }

      > .location-info-area {
        padding: 10px 20px;
        display: flex;
        align-items: center;

        input, input::placeholder, input:focus {
          font-size: 1rem;
          color: #fff;
          box-shadow: none;
          background: none;
          border: none;
        }
      }

      > .rate-area {
        padding: 0 20px;
        text-align: center;

        output {
          font-size: 2rem;
          color: #ffdd00;
          background: none;
          border: none;
          box-shadow: none;
        }
      }

      > .review-area {
        padding: 20px 10px;

        textarea, textarea::placeholder {
          min-height: 300px;
          resize: none;
          color: #fff;
          background: none;
          border: none;
          box-shadow: none;
        }

        /* width */
        ::-webkit-scrollbar {
          width: 10px;
        }

        /* Track */
        ::-webkit-scrollbar-track {
          background: #f1f1f1;
        }

        /* Handle */
        ::-webkit-scrollbar-thumb {
          background: #888;
        }

        /* Handle on hover */
        ::-webkit-scrollbar-thumb:hover {
          background: #555;
        }
      }
    }
  }

  > .side-bar-active-btn {
    flex-shrink: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #000000;
    padding: 0;
    border: none;
    border-radius: unset;
    color: #fff;
    opacity: 0.5;
    width: 40px;
    height: 40px;
  }
}
</style>
