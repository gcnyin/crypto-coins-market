let app = {
    data() {
        return {
            inputUsername: '',
            inputPassword: '',
            socket: null,
            token: null,
            logged: false,
            username: ''
        }
    },
    methods: {
        login() {
            axios({
                method: 'post',
                url: '/api/tokens',
                data: {
                    username: this.inputUsername,
                    password: this.inputPassword
                }
            }).then(d => {
                this.username = this.inputUsername
                this.inputUsername = ''
                this.inputPassword = ''
                this.logged = true
                this.token = d.data.token

                let protocol = document.location.protocol === "http:" ? 'ws' : 'wss'

                let socket = new WebSocket(protocol + '://' + document.location.host + '/api/ws?token=' + this.token)

                socket.addEventListener('open', function (event) {
                    socket.send('Hello Server!')
                });

                socket.addEventListener('message', function (event) {
                    console.log(event.data)
                });

                socket.addEventListener('close', function (e) {
                    console.log(e)
                });

                this.socket = socket
            })
        }
    }
}

Vue.createApp(app).mount('#app')
