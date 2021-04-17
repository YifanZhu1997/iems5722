from decimal import Decimal

from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import or_,and_,all_,any_
from flask import Flask, request
import json
import pymysql
import math
import requests
import redpackets

pymysql.install_as_MySQLdb()
app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://dbuser:password@127.0.0.1:3306/iems5722'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)

class User(db.Model):
    __tablename__="users"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(20))
    password = db.Column(db.String(20))
    wallet = db.Column(db.Float(10,2))

class RedEnvelope(db.Model):
    __tablename__="red_envelopes"
    id=db.Column(db.Integer, primary_key=True)
    message_id=db.Column(db.Integer)
    money=db.Column(db.Float(10,2))
    user_id = db.Column(db.Integer)

class Friends(db.Model):
    __tablename__="chatrooms_friends"
    chatroom_friends_id = db.Column(db.Integer, primary_key=True)
    user1 = db.Column(db.Integer)
    user2 = db.Column(db.Integer)

class Chatroom(db.Model):
    __tablename__ = "chatrooms"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(20))

class Message(db.Model):
    __tablename__ = "messages"
    id = db.Column(db.Integer, primary_key=True)
    chatroom_id = db.Column(db.Integer)
    user_id = db.Column(db.Integer)
    name = db.Column(db.String(20))
    message = db.Column(db.String(200))
    message_time = db.Column(db.TIMESTAMP)

    __mapper_args__ = {"order_by": id.desc()}


@app.before_first_request
def before_first_request():
    db.create_all()

@app.route('/api/a3/login',methods=['POST'])
def userLogin():
    result_dict=dict()
    user_id = request.form.get('user_id', type=int, default=None)
    password = request.form.get('password', type=str, default=None)

    if (user_id == None or password == None):
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'INVALID PARAMETER'
        return json.dumps(result_dict, sort_keys=False)

    user = db.session.query(User).filter(User.id == user_id).all()
    if(len(user)==0):
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'USER NOT EXIST'
        return json.dumps(result_dict, sort_keys=False)

    if(user[0].password != password):
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'PASSWORD NOT CORRECT'
        return json.dumps(result_dict, sort_keys=False)

    result_dict['status'] = 'OK'
    result_dict['name'] = user[0].name
    return json.dumps(result_dict, sort_keys=False)

@app.route('/api/a3/sign_up',methods=['POST'])
def userSignUp():
    result_dict = dict()
    name = request.form.get('name', type=str, default=None)
    password = request.form.get('password', type=str, default=None)
    user_id = request.form.get('user_id', type=int, default=None)

    if (name == None or password == None or user_id == None):
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'INVALID PARAMETER'
        return json.dumps(result_dict, sort_keys=False)

    user = db.session.query(User).filter(User.name == name).all()
    if (len(user) > 0):
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'USERNAME HAS ALREADY EXISTED'
        return json.dumps(result_dict, sort_keys=False)
    user2 = db.session.query(User).filter(User.id == user_id).all()
    if (len(user2) > 0):
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'USER_ID HAS ALREADY EXISTED'
        return json.dumps(result_dict, sort_keys=False)

    new_user = User(id = user_id,name = name, password = password,wallet = 200)
    db.session.add(new_user)
    db.session.commit()
    result_dict['status'] = 'OK'
    return json.dumps(result_dict, sort_keys=False)

@app.route('/api/a3/get_chatrooms', methods=['GET'])
def getChatrooms():
    chatrooms = db.session.query(Chatroom).all()
    data = []
    result_dict = dict()
    result_dict['status'] = 'OK'
    result_dict['data'] = data

    for chatroom in chatrooms:
        data.append({"id": chatroom.id, "name": chatroom.name})

    return json.dumps(result_dict, sort_keys=False)
@app.route('/api/a3/get_user', methods=['GET'])
def getUser():
    user_id = request.args.get('user_id', type=str, default=None)
    data = []
    result_dict = dict()
    if (user_id == None):
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'INVALID '
        return json.dumps(result_dict, sort_keys=False)
    else:
        user = db.session.query(User).filter(User.id == user_id).all()

        if len(user) == 0:
            result_dict['status'] = 'ERROR'
            result_dict['message'] = 'User Does not Exit'
            print('User Does not Exit')
            return json.dumps(result_dict, sort_keys=False)

        else:
            result_dict['status'] = 'OK'
            result_dict['data'] = data
            data.append({"id":user[0].id,"name":user[0].name})
            print(data)      
            return json.dumps(result_dict, sort_keys=False)

    result_dict['status'] = 'ERROR'
    result_dict['message'] = 'INVALID PARAMETER'
    return json.dumps(result_dict, sort_keys=False)
        
@app.route('/api/a3/check_friend', methods=['GET']) 
def checkFriend():
    user_id = request.args.get('user_id',type=str, default=None)
    friend_id = request.args.get('friend_id',type=str, default=None)
    print('USER_ID',user_id ,'FRIEND_ID',friend_id)
    data = []
    chatrooms = []
    result_dict = dict()
    if(user_id == None or friend_id == None or user_id == friend_id):
        print('INVALID PARAMETER')
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'INVALID PARAMETER'
        return json.dumps(result_dict, sort_keys=False)
    
    relation1 = db.session.query(Friends).filter(and_(Friends.user1 == user_id,Friends.user2 == friend_id)).all()
    relation2 = db.session.query(Friends).filter(and_(Friends.user1 == friend_id,Friends.user2 == user_id)).all()
    print(len(relation1))
    print(len(relation2))
    if (len(relation1)>0):
        
        result_dict['status'] = 'OK'
        result_dict['data'] = data
        #data['chatrooms'] = chatrooms
        data.append({"chatroom_id":relation1[0].chatroom_friends_id})
        print(data)
        return json.dumps(result_dict, sort_keys=False)
        
    if (len(relation2)>0):
        
        result_dict['status'] = 'OK'
        result_dict['data'] = data
        #data['chatrooms'] = chatrooms
        data.append({"chatroom_id":relation2[0].chatroom_friends_id})
        print(data)
        return json.dumps(result_dict, sort_keys=False)
        
    return json.dumps(result_dict, sort_keys=False)        
        

@app.route("/api/a3/add_friends", methods=['POST'])
def addfriends():
    result_dict = dict()
    user_id = request.form.get('user_id', type=int, default=None)
    friend_id = request.form.get('friend_id', type=int, default=None)

    if user_id == None or friend_id == None:
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'INVALID PARAMETER'
        return json.dumps(result_dict, sort_keys=False)

    new_friends = Friends(user1=user_id, user2=friend_id)
    db.session.add(new_friends)
    db.session.commit()
    result_dict['status'] = 'OK'
    return json.dumps(result_dict, sort_keys=False)  
    




@app.route('/api/a3/get_wallet', methods=['GET'])
def getWallet():
    user_id = request.args.get('user_id', type=str, default=None)
    user = db.session.query(User).filter(User.id == user_id).all()
    data = []
    result_dict = dict()
    result_dict['status'] = 'OK'
    result_dict['data'] = data
    data.append({"wallet": str(user[0].wallet.quantize(Decimal('0.00')))})

    return json.dumps(result_dict, sort_keys=False)

@app.route('/api/a3/get_friends', methods=['GET'])
def getfriends():
    id = request.args.get('user_id', type=str, default=None)

    friends_in_user1 = db.session.query(Friends).filter(Friends.user2 == id).all()
    friends_in_user2 = db.session.query(Friends).filter(Friends.user1 == id).all()

    data = dict()
    chatrooms = []
    friends_id = []
    friends_name = []
    result_dict = dict()

    result_dict['status'] = 'OK'
    result_dict['data'] = data
    data['friends_name'] = friends_name
    data['chatrooms'] = chatrooms
    for user in friends_in_user1:
        chatrooms.append(user.chatroom_friends_id)
        friends_id.append(user.user1)

    for user in friends_in_user2:
        chatrooms.append(user.chatroom_friends_id)
        friends_id.append(user.user2)

    for friend_id in friends_id:
        friends = db.session.query(User).filter(User.id == friend_id).all()
        if len(friends)!=0:
            friends_name.append(friends[0].name)

    return json.dumps(result_dict, sort_keys=False)

@app.route("/api/a3/get_messages", methods=['GET'])
def getMessages():
    result_dict = dict()
    chatroom_id = request.args.get('chatroom_id', type=int, default=None)
    current_page = request.args.get('page', type=int, default=None)
    if (chatroom_id == None or current_page == None):
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'INVALID PARAMETER'
        return json.dumps(result_dict, sort_keys=False)

    chatrooms = db.session.query(Chatroom).filter(Chatroom.id == chatroom_id).all()
    chatrooms_friend =db.session.query(Friends).filter(Friends.chatroom_friends_id == chatroom_id).all()
    if len(chatrooms) == 0 and len(chatrooms_friend) == 0:
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'CHATROOM NOT EXIST'
        return json.dumps(result_dict, sort_keys=False)

    messages =  db.session.query(Message).filter(Message.chatroom_id==chatroom_id).all()
    messages_size = len(messages)

    page_size = 8
    if messages_size == 0:
        total_page = 1
    else:
        total_page = int(math.ceil(messages_size / page_size))

    if current_page > total_page or current_page <= 0:
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'PAGE NOT EXIST'
        return json.dumps(result_dict, sort_keys=False)

    start = page_size * (current_page - 1)
    if total_page == 1:
        end = start + messages_size
    else:
        end = start + page_size

    data = dict()
    messages_data = []
    result_dict['status'] = 'OK'
    result_dict['data'] = data
    data['current_page'] = current_page
    data['messages'] = messages_data
    data['total_pages'] = total_page

    for message in messages[start:end]:
        messages_data.append(
            {'id': message.id, 'chatroom_id': message.chatroom_id,
             'user_id': message.user_id, 'name': message.name, 'message': message.message,
             'message_time': str(message.message_time.strftime('%Y-%m-%d %H:%M:%S'))})

    return json.dumps(result_dict, sort_keys=False)


@app.route("/api/a3/send_message", methods=['POST'])
def sendMessage():
    result_dict = dict()
    chatroom_id = request.form.get('chatroom_id', type=int, default=None)
    user_id = request.form.get('user_id', type=int, default=None)
    name = request.form.get('name', type=str, default=None)
    message = request.form.get('message', type=str, default=None)

    if chatroom_id == None or user_id == None or name == None or message == None:
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'INVALID PARAMETER'
        return json.dumps(result_dict, sort_keys=False)

    chatrooms = db.session.query(Chatroom).filter(Chatroom.id == chatroom_id).all()
    chatrooms_friend = db.session.query(Friends).filter(Friends.chatroom_friends_id == chatroom_id).all()

    if len(chatrooms) == 0 and len(chatrooms_friend) == 0:
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'CHATROOM NOT EXIST'
        return json.dumps(result_dict, sort_keys=False)

    new_message = Message(chatroom_id=chatroom_id, user_id=user_id, name=name, message=message)

    db.session.add(new_message)
    db.session.commit()

    new_message=db.session.query(Message).first()
    post_dict= {'id': new_message.id, 'chatroom_id': new_message.chatroom_id,
    'user_id': new_message.user_id, 'name': new_message.name, 'message': new_message.message,
    'message_time': str(new_message.message_time.strftime('%Y-%m-%d %H:%M:%S'))}

    url='http://3.17.158.90:8001/api/a4/broadcast_room'
    requests.post(url,post_dict,headers={'Content-Type':'application/x-www-form-urlencoded'})

    #if the mesage is a red envelope
    if len(new_message.message) > 22 and new_message.message[0:22]=='!@#$(RED_ENVELOPE)!@#$':
        red_envelope=json.loads(new_message.message[22:])
        total_money=red_envelope['total_money']
        number=red_envelope['number']
        money_list=redpackets.split(total_money, number, min=0.01)
        for money in money_list:
            new_envelope=RedEnvelope(message_id=new_message.id,money=float(money),user_id=-1)
            db.session.add(new_envelope)
            db.session.commit()

    result_dict['status'] = 'OK'
    return json.dumps(result_dict, sort_keys=False)

@app.route("/api/a3/fetch_red_envelope", methods=['GET'])
def fetchRedEnvelope():
    result_dict = dict()
    message_id = request.args.get('message_id', type=int, default=None)
    user_id = request.args.get('user_id', type=int, default=None)
    chatroom_id=request.args.get("chatroom_id",type=int,default=None)
    if (message_id == None or user_id == None or chatroom_id==None):
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'INVALID PARAMETER'
        return json.dumps(result_dict, sort_keys=False)

    #check if has been once fetched by a particular user
    red_envelopes = db.session.query(RedEnvelope).filter(and_(RedEnvelope.message_id == message_id,RedEnvelope.user_id==user_id)).all()
    if len(red_envelopes)>0:
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'ALREADY FETCHED ONCE'
        return json.dumps(result_dict, sort_keys=False)

    red_envelopes=db.session.query(RedEnvelope).filter(and_(RedEnvelope.message_id == message_id,RedEnvelope.user_id==-1)).all()
    if len(red_envelopes)==0:
        result_dict['status'] = 'ERROR'
        result_dict['message'] = 'EMPTY ENVELOPE'
        return json.dumps(result_dict, sort_keys=False)
    else:
        money=red_envelopes[0].money
        user=db.session.query(User).filter(User.id==user_id).first()
        new_wallet=user.wallet+money

        #mark this envelope has been fetched by this user_id
        if len(red_envelopes)>1:
            red_envelopes[0].user_id=user_id
            db.session.commit()
        else:
            #clear envelopes related to the message id if the envelope is empty
            db.session.query(RedEnvelope).filter(RedEnvelope.message_id==message_id).delete()
            db.session.commit()

        db.session.query(User).filter(User.id==user_id).update({User.wallet:float(new_wallet)})
        db.session.commit()

        result_dict['status'] = 'OK'
        result_dict['money']=str(money.quantize(Decimal('0.00')))
        return json.dumps(result_dict, sort_keys=False)

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)
