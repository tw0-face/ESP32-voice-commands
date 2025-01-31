import speech_commands as sp
from ubinascii import a2b_base64, b2a_base64

data = a2b_base64('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')
sp.init(data)
labels = ["ON","OFF","BLINK","[OTHER]"]
feature = bytearray(732)

def predict(audio):
    result = sp.predict(audio, 0, 0)
    return (labels[result // 1000], result % 1000)

def snapshot():
    global feature
    sp.export_mfcc(feature)

def save(label):
    with open('samples.txt', 'ab') as f:
        f.write(b'{"label": "')
        f.write(label.encode())
        f.write(b'", "mfcc": "')
        f.write(b2a_base64(feature)[:-1])
        f.write(b'"},\n')
