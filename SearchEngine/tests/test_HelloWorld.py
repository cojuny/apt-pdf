from src.HelloWorld import HelloWorld

def test_hello_world():
    hw = HelloWorld()
    assert hw.hello_world() == "Hello, World!"

