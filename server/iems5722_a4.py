import json
from flask import Flask, request
from flask_socketio import SocketIO, emit, join_room, leave_room, rooms
import eventlet
eventlet.monkey_patch()

app = Flask(__name__)
app.config['SECRET_KEY'] = 'iems5722'
socketio = SocketIO(app, async_mode='eventlet')

@app.route("/api/a4/broadcast_room", methods=["POST"])
def broadcast_room():
    result_dict=dict()
    id=request.form.get('id',type=int, default=None)
    chatroom_id = request.form.get('chatroom_id', type=int, default=None)
    user_id = request.form.get('user_id', type=int, default=None)
    name = request.form.get('name', type=str, default=None)
    message = request.form.get('message', type=str, default=None)
    message_time=request.form.get('message_time', type=str, default=None)

    new_message = dict(request.form)
    print(new_message)

    if id==None or chatroom_id == None or user_id == None or name == None or message == None or message_time==None:
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'INVALID PARAMETER'
        return json.dumps(result_dict, sort_keys=False)
    else:
        socketio.emit('new message', new_message, json=True, room=chatroom_id)
        result_dict['status'] = 'OK'
        return json.dumps(result_dict, sort_keys=False)

@socketio.on('connect')
def on_connect():
    print('user connected')

@socketio.on('user join')
def on_user_join(data):
    room=data['room']
    join_room(room)
    emit('user joined')
    print('user joined')

@socketio.on('disconnect')
def on_disconnect():
    rooms_=rooms()
    for room in rooms_:
        leave_room(room)
    print('user left')

if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0', port=8001, debug=True)