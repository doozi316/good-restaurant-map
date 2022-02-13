<template>
  <div id="app">
    <MainMap/>
    <SideBar class="side-bar"/>
  </div>
</template>

<script>
import MainMap from '@/components/MainMap'
import SideBar from '@/components/SideBar'

export default {
  name: 'App',
  components: {
    SideBar,
    MainMap
  }
}
</script>

<style lang="scss">
@import "~@/assets/scss/vendors/bootstrap-vue/index";
#app {
  font-family: 'Nanum Barun Gothic', serif;
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;

  > .side-bar {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
  }
}
</style>
