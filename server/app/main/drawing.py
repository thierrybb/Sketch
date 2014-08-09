import os
from flask import Blueprint, request, jsonify, session
from app.main import requires_auth
from app.main.user_provider import UserProvider
from app.models.UserDrawing import UserDrawing

__author__ = 'thierry'


drawing = Blueprint('drawing', __name__)


@drawing.route('/api/open_drawing', methods=['GET'])
def open_drawing():
    drawing_id = request.args.get('drawing')
    return jsonify({"event_bus": "192.168.2.125", "session_id" : drawing_id})


@drawing.route('/api/create_drawing', methods=['GET'])
@requires_auth
def create_drawing():
    provider = UserProvider()
    drawing_id = os.urandom(50).encode("base64")
    provider.create_drawing(session["email"], drawing_id)
    return jsonify({"bus": "192.168.2.125", "bus_port": 11112, "drawing_id" : drawing_id})

@drawing.route('/api/list_drawing', methods=['GET'])
@requires_auth
def list_drawing():
    drawings = []

    for drawing in UserDrawing.objects.find(Email=session["email"]):
        drawings.append(drawing.DrawingID)

    return jsonify(drawings)