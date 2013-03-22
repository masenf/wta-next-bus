import sys
import site
import os

ENVIRONPATH = "/srv/virtualenvs/wta-api-server/lib/python3.3/site-packages"
DEPLOYPATH = os.path.dirname(os.path.realpath(__file__))

# makes referencing static files easier (for now)
os.chdir(DEPLOYPATH)

prev_path = list(sys.path)
site.addsitedir(ENVIRONPATH)
sys.path.append(DEPLOYPATH)
new_path = [p for p in sys.path if p not in prev_path]
for item in new_path:
    sys.path.remove(item)
sys.path[:0] = new_path

import routes
application = routes.default_app()
