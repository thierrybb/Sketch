import os
from flask import Blueprint, request, jsonify, session
from app.main import requires_auth
from app.main.user_provider import UserProvider
from app.models.UserDrawing import UserDrawing
from config import config

__author__ = 'thierry'


drawing = Blueprint('drawing', __name__)


@drawing.route('/api/open_drawing', methods=['GET'])
@requires_auth
def open_drawing():
    drawing_id = request.args.get('drawing')
    return jsonify({'bus': config['event_bus']['address'], 'bus_port': config['event_bus']['port'], 'drawing_id' : drawing_id})


@drawing.route('/api/create_drawing', methods=['GET'])
@requires_auth
def create_drawing():
    provider = UserProvider()
    drawing_id = os.urandom(50).encode('base64').replace('\n', 'n')
    provider.create_drawing(session['email'], drawing_id)
    return jsonify({'bus': config['event_bus']['address'], 'bus_port': config['event_bus']['port'], 'drawing_id' : drawing_id})

@drawing.route('/api/list_drawing', methods=['GET'])
@requires_auth
def list_drawing():
    drawings = []

    for drawing in UserDrawing.objects.filter(Email=session['email']):
        drawings.append(drawing.DrawingID)

    return jsonify({'drawings': drawings})

@drawing.route('/api/add_collaborator', methods=['POST'])
@requires_auth
def add_collaborator():
    result = {'result': False}
    provider = UserProvider()
    email = request.form['email']
    drawing_id = request.form['drawingID']

    if provider.user_exist(email) and provider.drawing_exist(session['email'], drawing_id):
        result['result'] = True

        if provider.drawing_not_exist(email, drawing_id):
            provider.create_drawing(email, drawing_id)

    return jsonify(result)