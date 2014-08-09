from app.main.drawing import drawing


def create_app(host, port, bd_name):
    from flask import Flask

    app = Flask(__name__)
    app.secret_key = "r2Xmh]MAzVhK/+sdf#$#@%23refsdg%$^U&*^%%&^.sY9"

    from app.main.auth import auth
    app.register_blueprint(auth)
    app.register_blueprint(drawing)

    return Application(app, host, port, bd_name)


class Application:
    def __init__(self, app, host, port, bd_name):
        self._app = app
        self._host = host
        self._port = port
        self._bd_name = bd_name

    def run(self):
        from mongoengine import connect
        connect(self._bd_name)
        self._app.run(host=self._host ,port=self._port)

