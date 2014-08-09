__author__ = 'thierry'
from mongoengine import *


class UserDrawing(Document):
    Email = StringField()
    DrawingID = StringField()