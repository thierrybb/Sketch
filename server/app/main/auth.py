from flask import request, jsonify, session, Blueprint
from werkzeug.security import check_password_hash, generate_password_hash

auth = Blueprint('auth', __name__)

@auth.route('/')
def index():
    return "Hey!"


@auth.route('/api/login', methods=['POST'])
def login():
    result = {}
    from app.main.user_provider import UserProvider

    provider = UserProvider()
    email = request.form['email']
    password = request.form['password']

    if provider.user_not_exist(email):
        result["login"] = False
    else:
        user = provider.get_user(email)

        if check_password_hash(user.Password, password):
            session['email'] = user.Email
            result["login"] = True
            result["ID"] = user.ID

    return jsonify(result)


@auth.route('/api/can_access', methods=['POST'])
def can_access():
    result = {"access": False}
    from app.main.user_provider import UserProvider

    provider = UserProvider()
    email = request.form['email']
    password = request.form['password']
    drawing_id = request.form['drawingID']

    if provider.user_exist(email):
        user = provider.get_user(email)

        if check_password_hash(user.Password, password):
            if provider.drawing_exist(user.Email, drawing_id):
                result["access"] = True
                result["ID"] = user.ID

    return jsonify(result)


@auth.route('/api/register', methods=['POST'])
def register():
    email = request.form['email']
    password = generate_password_hash(request.form['password'])
    from app.main.user_provider import UserProvider

    provider = UserProvider()
    provider.lock()
    result = {}

    if provider.user_exist(email):
        result["register"] = False
        result["msg"] = "This user already exist"
    else:
        provider.create_user(email, "", password)
        result["register"] = True

    provider.unlock()

    return jsonify(result)


@auth.route('/api/logout', methods=['GET'])
def logout():
    session.pop('email', None)
    session.pop('nickname', None)
    return "true"