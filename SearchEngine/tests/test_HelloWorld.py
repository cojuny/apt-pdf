import unittest
from src.HelloWorld import HelloWorld

class TestHelloWorld(unittest.TestCase):
    def test_get_message(self):
        hw = HelloWorld()
        message = hw.hello_world()
        self.assertEqual(message, "Hello, World!")

if __name__ == '__main__':
    unittest.main()