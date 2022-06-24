export async function process(that, func) {
    that.processingCount++;
    try {
        return await func();
    } catch (err) {
        console.log(err.message);
        await that.$bvModal.msgBoxOk(err.message, {
            hideHeader: true,
            okTitle: '확인',
            noFade: false,
            size: 'sm',
            buttonSize: 'sm',
            okVariant: 'danger',
            headerClass: 'p-2 border-bottom-0',
            footerClass: 'p-2 border-top-0',
        })
        return await Promise.reject(err);
    } finally {
        that.processingCount--;
    }

}
