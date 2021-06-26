import RPi.GPIO as GPIO

def setupGPIO():
    GPIO.setmode(GPIO.BCM)
    GPIO.setwarnings(False)

    GPIO.setup(18, GPIO.OUT)


def toggle_led(state):
    try:
        if state == 'On':
            GPIO.output(18, GPIO.HIGH)
        elif state == 'Off':
            GPIO.output(18, GPIO.LOW)
    except:
        # Turn Off LED
        GPIO.output(18, GPIO.LOW)