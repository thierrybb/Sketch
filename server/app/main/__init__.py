from functools import wraps
from flask import Response, session

__author__ = 'thierry'

def requires_auth(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        if 'email' not in session:
            return Response('Nice try!', 403)
        return f(*args, **kwargs)

    return decorated