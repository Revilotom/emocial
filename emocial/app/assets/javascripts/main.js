
window.addEventListener('DOMContentLoaded', () => {
    const button = document.querySelector('#emoji-button');
    const picker = new EmojiButton({autoHide: false, position: "button"});

    picker.on('emoji', emoji => {
        let textarea = document.querySelector('textarea');
        let cursorPosition = $('textarea').prop("selectionStart");
        console.log(cursorPosition);
        textarea.value = textarea.value.substring(0, cursorPosition) + emoji + textarea.value.substring(cursorPosition);
    });

    if (button) {
        button.addEventListener('click', () => {
            picker.showPicker(button);
        });
    }
});

