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
      </div>
    </VueResizable>
    <BButton
        class="side-bar-active-btn"
        size="sm"
        @click="showSideBar"
    >
      {{ isVisibleSideBar ? '닫힘' : '열림' }}
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
      isVisibleSideBar: false
    }
  },
  methods: {
    showSideBar() {
      this.isVisibleSideBar = !this.isVisibleSideBar;
    }
  }
}
</script>

<style lang="scss" scoped>
.side-bar-wrapper {
  display: flex;

  > .resizable-side-bar {
    > .side-bar {
      background-color: #000000;
      opacity: 0.5;
      width: 100%;
      height: 100%;

      > .iw-title-area {

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
