import threading

from mongoengine import DoesNotExist
from app.models.User import User
from app.models.UserDrawing import UserDrawing

__author__ = 'thierry'

semaphore = threading.Semaphore()

class UserException(Exception):
    pass


class SynchronizedUserProvider(object):
    def __init__(self, arg):
        self.arg = arg

    def __call__(self, cls):
        class Wrapped(cls):
            classattr = self.arg

            def lock(self):
                semaphore.acquire(True)

            def unlock(self):
                semaphore.release()

        return Wrapped


@SynchronizedUserProvider("decorated class")
class UserProvider:
    def create_user(self, email, nickname, password):
        new_user = User()
        new_user.Email = email
        new_user.Nickname = nickname
        new_user.Password = password
        new_user.save()
        return new_user

    def user_exist(self, email):
        try:
            User.objects.get(Email=email)
        except DoesNotExist:
            return False

        return True

    def user_not_exist(self, nickname):
        return not self.user_exist(nickname)

    def get_user(self, email):
        try:
            user = User.objects.get(Email=email)
        except DoesNotExist:
            raise UserException

        return user

    def create_drawing(self, email, drawing_id):
        drawing = UserDrawing()
        drawing.Email = email
        drawing.DrawingID = drawing_id
        drawing.save()
        return drawing

    def drawing_exist(self, email, drawing_id):
        try:
            UserDrawing.objects.get(Email=email, DrawingID=drawing_id)
        except DoesNotExist:
            return False

        return True