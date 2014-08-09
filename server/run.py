# ----------------------------------------------------------------------------------------
# Auteur : Thierry Blais Brossard <thierry@blais-brossard.com>
# Description :
# Date : Juillet 2014
# ---------------------------------------------------------------------------------------
from app.factory import create_app

__author__ = 'thierry'

if __name__ == '__main__':
    app = create_app("0.0.0.0", 5000, "Sketch")

    app.run()