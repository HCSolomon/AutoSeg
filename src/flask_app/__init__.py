from flask import Flask

app = Flask(__name__)

@app.route('/')
def homepage():
    return "Hi there, I'm your future dashboard!"

if __name__ == "__main__":
    app.run(host='0.0.0.0')
