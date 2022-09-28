<template functional>
    <div class="progress-spinner">
        <BSpinner class="progress-spinner-icon" />
    </div>
</template>

<script>
export default {
    name: 'ProgressSpinner',
    functional: true,
};
</script>

<style lang="scss" scoped>
.progress-spinner {
    position: fixed;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    color: white;
    cursor: progress;
    z-index: 777;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: rgba(0, 0, 0, 0.3);
}
</style>
