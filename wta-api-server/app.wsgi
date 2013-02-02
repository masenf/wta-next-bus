import sys
import site
import os

DEPLOYPATH = "/home/deploy/wta-next-bus/wta-application-server"
ENVIRONPATH = "/srv/virtualenvs/wta-next-bus/lib/python3.3/site-packages"

prev_path = list(sys.path)
site.addsitedir(ENVIRONPATH)
sys.path.append(DEPLOYPATH)
new_path = [p for p in sys.path if p not in prev_path]
for item in new_path:
    sys.path.remove(item)
sys.path[:0] = new_path

import routes
application = routes.default_app()
