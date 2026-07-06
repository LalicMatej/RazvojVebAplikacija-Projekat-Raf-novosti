import { useState } from 'react';
import { Button, Badge } from 'react-bootstrap';
import type { ReactionResponse, ReactionType } from '../types';

interface Props {
    reactions: ReactionResponse;
    onReact: (type: ReactionType) => Promise<void>;
}

const ReactionBar = ({ reactions, onReact }: Props) => {
    const [loading, setLoading] = useState(false);

    const handleReact = async (type: ReactionType) => {
        if (loading) return;
        setLoading(true);
        try {
            await onReact(type);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="d-flex gap-3 align-items-center my-3">
            <Button
                variant={reactions.myReaction === 'LIKE' ? 'success' : 'outline-success'}
                size="sm"
                onClick={() => handleReact('LIKE')}
                disabled={loading}
            >
                👍 <Badge bg="light" text="dark">{reactions.likes}</Badge>
            </Button>
            <Button
                variant={reactions.myReaction === 'DISLIKE' ? 'danger' : 'outline-danger'}
                size="sm"
                onClick={() => handleReact('DISLIKE')}
                disabled={loading}
            >
                👎 <Badge bg="light" text="dark">{reactions.dislikes}</Badge>
            </Button>
        </div>
    );
};

export default ReactionBar;