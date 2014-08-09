from mongoengine import *

class User(Document):
    Email = StringField(primary_key=True)
    Nickname = StringField()
    Password = StringField()
    ID = SequenceField()


