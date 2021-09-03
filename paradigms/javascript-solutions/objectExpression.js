"use strict";

function primitiveOperandFactory(evaluate, diff) {
    let Operand = function (value) {
        this.value = value
    }

    Operand.prototype.toString = function () {
        return this.value.toString();
    }
    Operand.prototype.prefix = Operand.prototype.toString
    Operand.prototype.postfix = Operand.prototype.toString
    Operand.prototype.evaluate = evaluate;
    Operand.prototype.diff = diff;
    return Operand;
}

const Const = primitiveOperandFactory(
    function (_) {
        return this.value
    },
    (_) => constants.zero
)

const constants = {
    zero: new Const(0),
    one: new Const(1),
    E: new Const(Math.E),
    three: new Const(3),
    any: -1
}

const Variable = primitiveOperandFactory(
    function (...values) {
        return values[variables.indexOf(this.value)]
    },
    function (variable) {
        return (this.value === variable ? constants.one : constants.zero)
    }
)

const allowedOperations = {}

function AbstractOperation(toString, evaluate, diff, prefix, postfix) {
    let Operation = function (...operands) {
        this.operands = operands
    }
    Operation.prototype = Object.create(AbstractOperation.prototype);
    Operation.prototype.toString = toString;
    Operation.prototype.evaluate = evaluate;
    Operation.prototype.diff = diff;
    Operation.prototype.prefix = prefix;
    Operation.prototype.postfix = postfix;
    return Operation
}

function abstractOperationFactory(operationSign, operation, diffCalc) {
    let Operation = AbstractOperation(
        function () {
            return this.operands.map(op => op.toString()).join(" ") + " " + this.operationSign;
        },
        function (...values) {
            return this.operation(...this.operands.map(op => op.evaluate(...values)));
        },
        function (variable) {
            return this.diffCalc(...this.operands.slice(0, this.operands.length), ...this.operands.map(x => x.diff(variable)));
        },
        function () {
            return "(" + this.operationSign + " " + this.operands.map(op => op.prefix()).join(" ") + ")";
        },
        function () {
            return "(" + this.operands.map(op => op.postfix()).join(" ") + " " + this.operationSign + ")";
        }
    )

    Operation.prototype.operationSign = operationSign;
    Operation.prototype.operation = operation;
    Operation.prototype.diffCalc = diffCalc;
    if (operation.length === 0) {
        Operation.arity = constants.any;
    } else {
        Operation.arity = operation.length;
    }
    allowedOperations[operationSign] = Operation;

    return Operation;
}

const Add = abstractOperationFactory(
    '+',
    (a, b) => a + b,
    (a, b, da, db) => new Add(da, db)
)

const Subtract = abstractOperationFactory(
    '-',
    (a, b) => a - b,
    (a, b, da, db) => new Subtract(da, db)
)

const Multiply = abstractOperationFactory(
    '*',
    (a, b) => a * b,
    (a, b, da, db) => new Add(new Multiply(da, b), new Multiply(db, a))
)

const Sum = abstractOperationFactory(
    'sum',
    (...op) => op.reduce((accumulator, operand) => accumulator + operand, 0),
    (...op) => new Sum(...op.slice(op.length / 2))
)
Sum.arity = constants.any

const Divide = abstractOperationFactory(
    '/',
    (a, b) => a / b,
    (a, b, da, db) => new Divide(new Subtract(new Multiply(da, b), new Multiply(a, db)), new Multiply(b, b))
)


const Negate = abstractOperationFactory(
    'negate',
    (a) => -a,
    (a, da) => new Negate(da),
)

const Cube = abstractOperationFactory(
    'cube',
    (a) => Math.pow(a, 3),
    (a, da) => new Multiply(
        da,
        new Multiply(
            constants.three,
            new Multiply(a, a))),
)

const Cbrt = abstractOperationFactory(
    'cbrt',
    (a) => Math.cbrt(a),
    (a, da) => new Divide(
        da,
        new Multiply(
            constants.three,
            new Multiply(
                new Cbrt(a),
                new Cbrt(a)
            )
        )
    )
)

const variables = ['x', 'y', 'z']

const diffSumsq = function (...op) {
    let ops = []
    for (let i = 0; i < op.length / 2; i++) {
        ops.push(new Multiply(op[i], op[op.length / 2 + i]))
    }
    return new Multiply(new Const(2), new Sum(...ops))
}

const Sumsq = abstractOperationFactory(
    'sumsq',
    (...op) => (op.map(op => op * op).reduce((accumulator, operand) => accumulator + operand, 0)),
    (...op) => diffSumsq(...op)
)

const Length = abstractOperationFactory(
    'length',
    (...op) => Math.sqrt(op.map(op => op * op).reduce((accumulator, operand) => accumulator + operand, 0)),
    (...op) => op.length === 0 ? constants.zero : new Multiply(
        new Divide(
            new Const(1),
            new Multiply(
                new Const(2),
                new Length(...op.slice(0, op.length / 2)
                )
            )
        ),
        diffSumsq(...op)
    )
)


const parse = expression => {
    let stack = []
    for (const el of expression.trim().split(/\s+/)) {
        if (isFinite(el)) {
            stack.push(new Const(parseFloat(el)))
        } else if (variables.includes(el)) {
            stack.push(new Variable(el))
        } else if (el in allowedOperations) {
            const operands = stack.splice(-(allowedOperations[el]).arity);
            let f = allowedOperations[el]
            stack.push(new f(...operands));
        }
    }
    return stack[0];
}


const exceptionFactory = function (Message, name) {
    const exc = function (...args) {
        this.message = Message(...args)
        this.name = name
    }

    exc.prototype = new Error
    return exc
}

const UnexpectedCharacterException = exceptionFactory(
    (index, currentChar, expected) => "Expected '" + expected + "', found '" + currentChar + "' at position " + index,
    "UnexpectedCharacterException"
)
const InvalidExpressionException = exceptionFactory(
    (currentChar) => "Unexpected character '" + currentChar + "' after the end of the expression",
    "InvalidExpressionException"
)
const InvalidOperandException = exceptionFactory(
    (currentChar) => "Invalid operand '" + currentChar + "'",
    "InvalidOperandException"
)
const ArityException = exceptionFactory(
    (arity, len) => "Expected " + arity + " operands, found " + len,
    "ArityException"
)
const InvalidOperationException = exceptionFactory(
    (sign) => "Invalid operation '" + sign + "'",
    "InvalidOperationException"
)

const BaseParser = function () {
    let currentIndex, expression
    this.setSource = function (data) {
        currentIndex = 0
        expression = data
    }
    this.hasNext = () => currentIndex < expression.length;
    this.next = () => expression[currentIndex++];
    this.current = () => expression[currentIndex];
    this.nextChar = function () {
        currentIndex++
    }
    this.test = function (expected) {
        if (this.current() === expected) {
            this.nextChar()
            return true
        }
        return false
    }
    this.expect = function (c) {
        if (this.current() !== c) {
            throw new UnexpectedCharacterException(currentIndex + 1, this.current(), c).toString()
        }
        this.nextChar()
    }
    this.equals = (c) => this.current() === c;
}

const ExpressionParser = function () {
    BaseParser.call(this);
    this.splitByWs = function (str) {
        let res = [];
        for (let i = 0; i < str.length; i++) {
            if (str[i] === '(' || str[i] === ')') {
                res.push(str[i])
            } else if (str[i] !== ' ') {
                let wordStart = i;
                while (str[i] !== ' ' && str[i] !== '(' && str[i] !== ')' && i < str.length) {
                    i++;
                }
                res.push(str.substring(wordStart, i--));
            }
        }
        return res;
    }
    this.parse = function (expression, mode) {
        let expr = this.splitByWs(expression)
        expr = (mode === 'postfix') ? expr.reverse().map(el => el === '(' ? ')' : (el === ')' ? '(' : el)).join(" ") : expr
        this.setSource(expr)
        let ans = this.parseExpression(mode)
        this.skipWhitespaces()
        if (this.hasNext()) {
            throw new InvalidExpressionException(this.current()).toString()
        }
        return ans
    }
    this.parseExpression = function (mode) {
        this.skipWhitespaces()
        return this.test('(') ? this.parseOperation(mode) : this.parseSingleOperand()
    }
    this.nextToken = function () {
        let res = ''
        while (this.hasNext() && !this.equals(' ') && !this.equals('(') && !this.equals(')')) {
            res += this.current()
            this.nextChar()
        }
        return res;
    }
    this.parseSingleOperand = function () {
        let res = this.nextToken()
        if (isFinite(res) && res !== '') {
            return new Const(parseFloat(res))
        } else if (variables.includes(res)) {
            return new Variable(res)
        } else {
            throw new InvalidOperandException(res).toString()
        }
    }
    this.skipWhitespaces = function () {
        while (this.test(' ')) {
        }
    }
    this.parseOperation = function (mode) {
        this.skipWhitespaces()
        let sign = this.nextToken()
        if (sign in allowedOperations) {
            let op = allowedOperations[sign]
            let a = []
            this.skipWhitespaces()
            if (!(op.arity === constants.any && this.test(')'))) {
                while (this.hasNext() && this.current() !== ')') {
                    a.push(this.parseExpression(mode))
                    this.skipWhitespaces()
                }
                this.expect(')')
                if (a.length !== op.arity && op.arity !== constants.any) {
                    throw new ArityException(op.arity, a.length).toString()
                }
                if (mode === 'postfix') {
                    a.reverse()
                }
            }
            return new op(...a)
        } else {
            throw new InvalidOperationException(sign).toString()
        }
    }
}
let parser = new ExpressionParser()
const parsePrefix = function (s) {
    return parser.parse(s, 'prefix');
}
const parsePostfix = function (s) {
    return parser.parse(s, 'postfix');
}