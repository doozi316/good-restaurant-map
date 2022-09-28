export function confirm(that, message) {
    return that.$bvModal.msgBoxConfirm(message, {
        hideHeader: true,
        okTitle: '확인',
        cancelTitle: '취소',
        noFade: false,
        size: 'sm',
        buttonSize: 'sm',
        okVariant: 'warning',
        cancelVariant: 'secondary',
        headerClass: 'p-2 border-bottom-0',
        footerClass: 'p-2 border-top-0',
    });
}

export function ok(that, message) {
    return that.$bvModal.msgBoxOk(message, {
        hideHeader: true,
        okTitle: '확인',
        noFade: false,
        size: 'sm',
        buttonSize: 'sm',
        okVariant: 'success',
        headerClass: 'p-2 border-bottom-0',
        footerClass: 'p-2 border-top-0',
    });
}
